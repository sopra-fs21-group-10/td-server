package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Tower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("towerRepository")
public interface TowerRepository extends JpaRepository<Tower, Long> {
    Tower findByTowerName(String name);
}
