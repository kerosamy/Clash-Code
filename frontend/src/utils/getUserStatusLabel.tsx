import { UserStatus } from "../enums/UserStatus";

export const getStatusLabel = (status: UserStatus) => {
    switch (status) {
        case UserStatus.ONLINE: return 'Online';
        case UserStatus.IN_MATCH: return 'In Match';
        case UserStatus.OFFLINE: return 'Offline';
    }
};