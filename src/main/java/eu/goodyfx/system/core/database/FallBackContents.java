package eu.goodyfx.system.core.database;

import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

@Getter
public enum FallBackContents {

    REQUEST_ACCEPTED_SINCE("User.%s.allowed-since") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setAllowed_since);
        }
    },
    REQUEST_STATE("User.%s.state") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, Boolean.class, user::setState);
        }
    },
    REQUEST_ACCEPTED_BY("User.%s.accepted_By") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setAllowed_by);
        }
    },
    REQUEST_DENIED("User.%s.reason") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setDeny_reason);
        }
    },

    FIRST_DOC("User.%s.firstDoc") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, Long.class, user::setFirst_join);
        }
    },
    PLAYER_COLOR("User.%s.playerColor") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setColor);
        }
    },
    USERNAMES("User.%s.userNames") {
        @Override
        public void apply(RaspiUsernames user, String path, FallBackManager manager) {
            fetch(manager, path, List.class, user::setUsernames);
        }
    },
    LAST("User.%s.last") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, Long.class, user::setLastSeen);
        }
    },
    PREFIX("User.%s.prefix") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setPrefix);
        }
    },
    DENY_PERSON("User.%s.denyPerson") {
        @Override
        public void apply(RaspiUser user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setDenied_by);
        }
    },
    MUTED("User.%s.muted") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, Boolean.class, user::setMuted);
        }
    },
    MUTED_OWNER("User.%s.muteOwner") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setMute_owner);
        }
    },
    MUTED_REASON("User.%s.muteReason") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setMute_message);
        }
    },
    BANNED("User.%s.ban.tempBaned") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, Boolean.class, user::setBanned);
        }
    },
    BAN_REASON("User.%s.ban.reason") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setBan_message);
        }
    },
    BAN_PERFORMER("User.%s.ban.performer") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, String.class, user::setBan_owner);
        }
    },
    BAN_EXPIRE("User.%s.ban.expire") {
        @Override
        public void apply(RaspiManagement user, String path, FallBackManager manager) {
            fetch(manager, path, Long.class, user::setBan_expire);
        }
    },
    SETTING_AUTO_AFK("User.%s.autoAFK") {
        @Override
        public void apply(UserSettings user, String path, FallBackManager manager) {
            fetch(manager, path, Boolean.class, user::setAuto_afk);
        }
    },
    SETTING_SERVER_MESSAGES("User.%s.messages") {
        @Override
        public void apply(UserSettings user, String path, FallBackManager manager) {
            fetch(manager, path, Boolean.class, user::setServer_messages);
        }
    },
    SETTING_OPT_CHAT("User.%s.optChat") {
        @Override
        public void apply(UserSettings user, String path, FallBackManager manager) {
            fetch(manager, path, Boolean.class, user::setOpt_chat);
        }
    };

    private final String path;

    FallBackContents(String path) {
        this.path = path;
    }

    public void apply(RaspiManagement user, String path, FallBackManager manager) {
    }

    public void apply(RaspiUser user, String path, FallBackManager manager) {
    }

    public void apply(UserSettings user, String path, FallBackManager manager) {
    }

    public void apply(RaspiUsernames user, String path, FallBackManager manager) {
    }


    protected <T> void fetch(FallBackManager manager, String path, Class<T> type, Consumer<T> setter) {
        T value = manager.getAndRemove(path, type);
        if (value != null) {
            setter.accept(value);
            manager.foundDataDebugMessage(path);
        }
    }

}
