package com.fit.nufit.member.domain;

public enum SocialType {
    NAVER, KAKAO;

    public static SocialType from(String value) {
        return SocialType.valueOf(value.toUpperCase());
    }

    public boolean isNaver() {
        return this == NAVER;
    }
}
