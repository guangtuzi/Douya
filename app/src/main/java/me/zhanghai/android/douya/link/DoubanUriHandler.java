/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;

import java.util.List;

import me.zhanghai.android.douya.broadcast.ui.BroadcastActivity;
import me.zhanghai.android.douya.broadcast.ui.BroadcastListActivity;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.util.UriUtils;

public class DoubanUriHandler {

    private static final String AUTHORITY = "www.douban.com";
    private static final String AUTHORITY_FRODO = "douban.com";

    private enum UriType {

        USER_BROADCAST_LIST("people/*/statuses"),
        TOPIC_BROADCAST_LIST("update/topic/*"),
        BROADCAST("people/*/status/#"),
        BROADCAST_FRODO(AUTHORITY_FRODO, "status/#"),
        USER("people/*");

        String mAuthority;
        String mPath;

        UriType(String authority, String path) {
            mAuthority = authority;
            mPath = path;
        }

        UriType(String path) {
            this(AUTHORITY, path);
        }

        public String getAuthority() {
            return mAuthority;
        }

        public String getPath() {
            return mPath;
        }
    }

    private static final UriMatcher MATCHER;
    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        for (UriType uriType : UriType.values()) {
            MATCHER.addURI(uriType.getAuthority(), uriType.getPath(), uriType.ordinal());
        }
    }

    private DoubanUriHandler() {}

    public static boolean open(Uri uri, Context context) {

        int code = MATCHER.match(uri);
        if (code == UriMatcher.NO_MATCH) {
            return false;
        }
        UriType uriType = UriType.values()[code];

        Intent intent;
        switch (uriType) {
            case USER_BROADCAST_LIST: {
                List<String> segments = uri.getPathSegments();
                intent = BroadcastListActivity.makeIntent(segments.get(segments.size() - 2),
                        context);
                break;
            }
            case TOPIC_BROADCAST_LIST:
                intent = BroadcastListActivity.makeIntent(uri.getLastPathSegment(), false, context);
                break;
            case BROADCAST:
            case BROADCAST_FRODO:
                intent = BroadcastActivity.makeIntent(UriUtils.parseId(uri), context);
                break;
            case USER:
                intent = ProfileActivity.makeIntent(uri.getLastPathSegment(), context);
                break;
            default:
                return false;
        }

        context.startActivity(intent);
        return true;
    }

    public static boolean open(String uri, Context context) {
        return open(Uri.parse(uri), context);
    }
}
