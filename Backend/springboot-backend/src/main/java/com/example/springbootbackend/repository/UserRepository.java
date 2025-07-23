package com.example.springbootbackend.repository;



import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.springbootbackend.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    @Query("SELECT address FROM User u WHERE u.userId = :userID")
    String GetUsersAddress(@Param("userID") UUID userID);

    @Query("SELECT u FROM User u WHERE u.userId = :userID")
    User findUsersByUserID(@Param("userID") UUID userID);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userID")
    Integer CheckUserID(@Param("userID") UUID userID);


    @Query("SELECT role FROM User u WHERE u.userId = :userID")
    String GetUserRole(@Param("userID") UUID userID);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.userId = :userID")
    Integer DeleteUser(@Param("userID") UUID userID);


     


    @Modifying
    @Transactional
    @Query(value = """
        WITH input AS (
            SELECT * FROM json_populate_record(NULL::"user", cast(:jsonData AS json))
        )
        UPDATE "user"
        SET name = COALESCE(input.name, "user".name),
            email = COALESCE(input.email, "user".email),
            phone_number = COALESCE(input.phone_number, "user".phone_number),
            username = COALESCE(input.username, "user".username),
            role = COALESCE(input.role, "user".role),
            address = COALESCE(input.address, "user".address)


        FROM input
        WHERE "user".user_id = :id
        """, nativeQuery = true)
    Integer UpdateUserInfo(@Param("jsonData") String jsonData, @Param("id") UUID id);



    
}
