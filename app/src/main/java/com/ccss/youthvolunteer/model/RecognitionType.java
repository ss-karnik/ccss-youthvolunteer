package com.ccss.youthvolunteer.model;

import com.google.common.collect.Lists;

import java.util.List;

public enum RecognitionType{
    Badge, Medal, Title, Trophy, Voucher;

    private static final List<String> recognitionTypes;
    static {
        recognitionTypes = Lists.newArrayList();
        for (RecognitionType value : RecognitionType.values())
            recognitionTypes.add(value.toString());
    }
    public static List<String> toArrayList () { return recognitionTypes; }
}

