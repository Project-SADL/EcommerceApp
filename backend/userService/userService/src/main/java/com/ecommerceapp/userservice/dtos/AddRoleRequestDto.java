package com.ecommerceapp.userservice.dtos;

import com.ecommerceapp.userservice.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AddRoleRequestDto {
    List<Role> roles;
}
