package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원명은 필수 입력사항 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
