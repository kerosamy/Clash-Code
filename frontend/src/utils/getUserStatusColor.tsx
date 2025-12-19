import { UserStatus } from "../enums/UserStatus";

export const getStatusColor = (status: UserStatus) => {
    switch (status) {
        case UserStatus.ONLINE:
            return 'green-500';
        case UserStatus.IN_MATCH:
            return 'yellow-500';
        case UserStatus.OFFLINE:
        default:
            return 'gray-400';
    }
};