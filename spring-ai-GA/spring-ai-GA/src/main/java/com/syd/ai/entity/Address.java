package com.syd.ai.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Address {
    String name;        // 收件人姓名
    String phone;        // 联系电话
    String province;     // 省
    String city;         // 市
    String district;     // 区/县
    String detail;        // 详细地址
}
