package com.clear.zero.domain.model.courseitem;

import lombok.*;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CourseItemNumber implements Serializable {
    private String value;

    public static CourseItemNumber of(Integer categoryId) {
        checkArgument(categoryId != null, "课程类目不能为空");
        checkArgument(categoryId > 0, "课程类目id不能小于0");
        return new CourseItemNumber(generateItemNo(categoryId));
    }

    public static CourseItemNumber of(String value) {
        checkArgument(!StringUtils.isEmpty(value), "课程编码不能为空");
        return new CourseItemNumber(value);
    }

    private static String generateItemNo(Integer categoryId) {
        String prefix = "COURSE";
        String typeStr = String.format("%04d", categoryId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currentTime = sdf.format(new Date());
        int randomNum = (int) (Math.random() * 9999 + 1);
        String randomNumStr = String.format("%04d", randomNum);
        return prefix + typeStr + currentTime + randomNumStr;
    }
}
