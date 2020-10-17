package com.clear.zero.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author fengjian
 * @version Ver1.0
 * @description 优化了Twitter的ID生成
 * @date 2020/1/10 16:41
 **/
@Slf4j
public class SnowflakeIDUtil {
    /**
     * 开始时间,2018-01-01 00:00:00
     */
    private static final long START_TIME = 1514736000000L;
    /**
     * 机器ID所占大小
     */
    private static final long WORKER_ID_BITS = 5L;
    /**
     * 数据标识id所占的位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /**
     * 支持的最大机器id(十进制),结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     * -1L 左移 5位 (worker id 所占位数) 即 5位二进制所能获得的最大十进制数 - 31
     */
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    /**
     * 支持的最大数据标识id - 31
     */
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);
    /**
     * 序列在id中占的位数
     */
    private static final long SEQUENCE_BITS = 12L;
    /**
     * 机器ID 左移位数 - 12 (即末 sequence 所占用的位数)
     */
    private static final long WORKER_ID_MOVE_BITS = SEQUENCE_BITS;
    /**
     * 数据标识id 左移位数 - 17(12+5)
     */
    private static final long DATA_CENTER_ID_MOVE_BITS = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间截向 左移位数 - 22(5+5+12)
     */
    private static final long TIMESTAMP_MOVE_BITS = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    /**
     * 生成序列的掩码(12位所对应的最大整数值),这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
    /**
     * 默认的机器ID所占大小
     */
    private static final long DEFAULT_WORKER_ID = 27L;
    /**
     * 默认的数据标识ID
     */
    private static final long DEFAULT_DATA_CENTER_ID = 17L;
    private static volatile SnowflakeIDUtil idWorker = null;
    /**
     * 工作机器ID(0~31)
     */
    private long workerId;
    /**
     * 数据中心ID(0~31)
     */
    private long dataCenterId;
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    private SnowflakeIDUtil(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    private SnowflakeIDUtil() {
        //获取机器编码
        this.workerId = this.getMachineNum();
        //获取进程编码
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        try {
            this.dataCenterId = Long.parseLong(runtimeMXBean.getName().split("@")[0]);
        } catch (Exception e) {
            this.dataCenterId = DEFAULT_DATA_CENTER_ID;
            log.error("雪花算法获取进程编码出错:{}", e);
        }
        log.info("workerId:{},dataCenterId:{}", workerId, dataCenterId);
        //避免编码超出最大值(workerId:2^5;dataCenterId:2^5)
        this.workerId = workerId & MAX_WORKER_ID;
        this.dataCenterId = dataCenterId & MAX_DATA_CENTER_ID;
    }

    /**
     * @return com.king.utils.SnowflakeIDUtil
     * @description 采用DCL进行单例实现
     **/
    public static SnowflakeIDUtil getInstance() {
        try {
            if (idWorker != null) {
                return idWorker;
            } else {
                Thread.sleep(200);
                synchronized (SnowflakeIDUtil.class) {
                    if (null == idWorker) {
                        idWorker = new SnowflakeIDUtil();
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取SnowflakeIDUtil实例出错:{}", e);
        }
        return idWorker;
    }

    /**
     * 简化ID获取方式
     * @return
     */
    public static long getId() {
        return SnowflakeIDUtil.getInstance().nextId();
    }

    public static void main(String[] args) {
        System.out.println(SnowflakeIDUtil.getInstance().nextId());
    }

    /**
     * @return long
     * @description 线程安全的获得下一个 ID 的方法
     **/
    public synchronized long nextId() {
        long timestamp = currentTime();
        //如果当前时间小于上一次ID生成的时间戳: 说明系统时钟回退过 - 这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            log.error("系统时钟回退,生成ID出错,上次生成ID时间{},当前时间{}", lastTimestamp, timestamp);
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生成的,则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出 即 序列 > 4095
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = blockTillNextMillis(lastTimestamp);
            }
        }
        //时间戳改变,毫秒内序列重置
        else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIME) << TIMESTAMP_MOVE_BITS)
                | (dataCenterId << DATA_CENTER_ID_MOVE_BITS)
                | (workerId << WORKER_ID_MOVE_BITS)
                | sequence;
    }

    /***
     * @description 阻塞到下一个毫秒 即 直到获得新的时间戳
     **/
    private long blockTillNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    /**
     * @return long
     * @description 获得以毫秒为单位的当前时间
     **/
    private long currentTime() {
        return System.currentTimeMillis();
    }

    private long getMachineNum() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            log.error("雪花算法获取机器编码出错:{}", e1);
        }
        if (null == e) {
            return DEFAULT_WORKER_ID;
        } else {
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
            }
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }

}
