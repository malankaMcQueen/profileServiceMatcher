package com.example.matcher.profileservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusConnectionUpdate {
    UUID userId;
    StatusConnection statusConnection;
}
