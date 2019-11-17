package com.itlike.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter@Getter@ToString
public class Role {
    private Long rid;

    private String rnum;

    private String rname;

    /*一个角色可以有多个权限*/
    public List<Permission> permissions = new ArrayList<>();


}