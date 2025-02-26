package com.example.matcher.profileservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserIdsRequest {
    private List<String> userIds;
}