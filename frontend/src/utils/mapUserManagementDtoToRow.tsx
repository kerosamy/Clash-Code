import type { UserManagementDto } from "../services/UserService";
import type { UserManagementRowProps } from "../components/common/UserManagementRow";

export function mapUserDtoToRowProps(
    user: UserManagementDto,
    index: number,
    page: number,
    pageSize: number = 20
): UserManagementRowProps {
    return {
        order: page * pageSize + index + 1,
        id: user.id,
        username: user.username,
        email: user.email,
        role: user.role,
        rank: user.rank,
    };
}