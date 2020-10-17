package com.clear.zero.domain.common.model;

import com.clear.zero.domain.exception.InvalidParameterException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

import static com.google.common.base.Preconditions.checkArgument;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Price implements Serializable {

    @Convert(converter = CurrencyConverter.class)
    @Column(name = "currency_code", length = 3)
    private Currency currency;
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    public static Price of(String currencyCode, BigDecimal value) {
        checkArgument(!StringUtils.isEmpty(currencyCode), "币种不能为空");
        Currency currency;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException(String.format("【%s】不是有效的币种", currencyCode));
        }
        if ("CAD".equalsIgnoreCase(currencyCode)) {
            throw new InvalidParameterException(String.format("【%s】对不起，暂不支持该币种", currencyCode));
        }
        checkArgument(value != null, "价格不能为空");
        checkArgument(value.compareTo(BigDecimal.ZERO) > 0, "价格必须大于0");
        return new Price(currency, value);
    }
}
