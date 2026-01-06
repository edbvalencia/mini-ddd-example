package com.alutarb.shared.domain;

public enum SocialNetwork {
    FACEBOOK,
    INSTAGRAM,
    REDDIT,
    TELEGRAM,
    TIKTOK,
    TWITCH,
    TWITTER,
    YOUTUBE;

    public static SocialNetwork fromString(String name) {
        if ("X".equalsIgnoreCase(name)) {
            return TWITTER;
        }
        return SocialNetwork.valueOf(name.toUpperCase());
    }

}
