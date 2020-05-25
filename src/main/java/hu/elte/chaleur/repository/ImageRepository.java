package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Image findByName(String name);
}
