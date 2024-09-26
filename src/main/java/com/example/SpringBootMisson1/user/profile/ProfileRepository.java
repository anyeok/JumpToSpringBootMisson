package com.example.SpringBootMisson1.user.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("select distinct P from Profile P " +
            "left join P.author U " +
            "where U.id = :userId " +
            "and (U.username like %:keyword% " +
            "or U.nickname like %:keyword% " +
            "or U.email like %:keyword%)")
    List<Profile> findAllByUserIdAndKeyword(@Param("userId") Long id, @Param("keyword") String keyword);
}