package dev.zaco.MariaDB_testing.repositories;

import dev.zaco.MariaDB_testing.models.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
