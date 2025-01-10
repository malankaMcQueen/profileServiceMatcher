package com.example.matcher.profileservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StatusConnectionUpdate {
    UUID userId;
    StatusConnection statusConnection;
}
