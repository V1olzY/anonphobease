package com.vizako.anonphobease.model;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class RelatedEntityInfo {
    private final String name;
    private final String extra;


    public String getName() { return name; }
    public String getExtra() { return extra; }
}
