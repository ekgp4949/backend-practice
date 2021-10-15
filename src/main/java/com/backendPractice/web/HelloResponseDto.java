package com.backendPractice.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class HelloResponseDto {
    private final String name;
    private final int amount;
}
