import { UserStatus } from "../enums/UserStatus";

export const getStatusColor = (status: UserStatus) => {
  switch (status) {
    case UserStatus.ONLINE:
      return "text-green-500";
    case UserStatus.IN_MATCH:
      return "text-yellow-500";
    case UserStatus.OFFLINE:
    default:
      return "text-gray-400";
  }
};