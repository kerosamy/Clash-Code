import { useEffect, useState } from "react";
import Board from "../components/common/Board";
import UserManagementRow, { type UserManagementRowProps } from "../components/common/UserManagementRow";
import SearchBar from "../components/common/SearchBar";
import UserRoleFilter from "../components/common/UserRoleFilter";
import { 
  getAllUsers, 
  searchUsersByUsername, 
  getFilteredUsersByRole,
  promoteUserToAdmin, 
  demoteUserToUser,
} from "../services/UserService";
import { useNavigate } from "react-router-dom";
import { mapUserDtoToRowProps } from "../utils/mapUserManagementDtoToRow";


export default function UserManagement() {
  const [users, setUsers] = useState<UserManagementRowProps[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedRole, setSelectedRole] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();


  async function loadUsers(pageToLoad = 0) {
    setLoading(true);
    try {
      let backendPage;
      
      if (searchQuery.trim() !== "") {
        // Search takes priority
        backendPage = await searchUsersByUsername(searchQuery, pageToLoad, 20);
      } else if (selectedRole) {
        // Filter by role
        backendPage = await getFilteredUsersByRole(selectedRole, pageToLoad, 20);
      } else {
        // Get all users
        backendPage = await getAllUsers(pageToLoad, 20);
      }

      const mapped = backendPage.content.map((user, idx) => 
        mapUserDtoToRowProps(user, idx, pageToLoad)
      );
      setUsers(mapped);
      setPage(pageToLoad);
      setTotalPages(backendPage.totalPages);
    } catch (err) {
      console.error("Failed to fetch users", err);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadUsers();
  }, [searchQuery, selectedRole]);

  const handlePromote = async (user: UserManagementRowProps) => {
    try {
      await promoteUserToAdmin(user.id);
      console.log("User promoted:", user.username);
      loadUsers(page);
    } catch (error) {
      console.error("Failed to promote user:", error);
    }
  };

  const handleDemote = async (user: UserManagementRowProps) => {
    try {
      await demoteUserToUser(user.id);
      console.log("User demoted:", user.username);
      loadUsers(page);
    } catch (error) {
      console.error("Failed to demote user:", error);
    }
  };

  const handleUsernameClick = (user: UserManagementRowProps) => {
    navigate(`/profile/${user.username}/overview`);
  };

  const handlePrevPage = () => {
    if (page > 0) loadUsers(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadUsers(page + 1);
  };

  return (
    <div className="flex flex-col h-[90vh] space-y-4 p-scroll-x">
      <div className="flex items-center justify-between flex-wrap space-y-4">
        <SearchBar
          value={searchQuery}
          onChange={setSearchQuery}
          placeholder="Search users by username..."
        />

        <UserRoleFilter
          value={selectedRole}
          onChange={setSelectedRole}
        />
      </div>

      <div className="flex-1 overflow-y-auto custom-scroll">
        <Board<UserManagementRowProps>
          data={users}
          columns={["#", "Username", "Email", "Rank", "Role", "Actions"]}
          renderRow={(user) => (
            <UserManagementRow
              key={user.id}
              {...user}
              onPromoteClick={() => handlePromote(user)}
              onDemoteClick={() => handleDemote(user)}
              onUsernameClick={() => handleUsernameClick(user)}
              className="cursor-pointer"
            />
          )}
          gridCols="grid-cols-[60px_2fr_2fr_1fr_1fr_100px]"
        />
      </div>

      <div className="flex justify-center gap-4">
        <button
          onClick={handlePrevPage}
          disabled={page === 0 || loading}
          className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 disabled:hover:bg-sidebar/50 disabled:hover:border-white/10 disabled:cursor-not-allowed transition-all duration-300 font-anta text-sm"
        >
          Previous
        </button>

        <span className="flex items-center text-text/80 font-anta text-sm bg-sidebar/30 px-4 rounded-full border border-white/5">
          Page {page + 1} of {totalPages}
        </span>

        <button
          onClick={handleNextPage}
          disabled={page >= totalPages - 1 || loading}
          className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 disabled:hover:bg-sidebar/50 disabled:hover:border-white/10 disabled:cursor-not-allowed transition-all duration-300 font-anta text-sm"
        >
          Next
        </button>
      </div>
    </div>
  );
}