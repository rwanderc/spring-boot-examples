package example.service;

import example.model.Car;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CarService {

    private final CarRepository repository;

    @Cacheable(value = "carList")
    public List<Car> findAll() {
        log.info("CarService.findAll()");
        return IteratorUtils.toList(repository.findAll().iterator());
    }

    @Cacheable(value = "cars")
    public Car findOne(Long id) {
        log.info("CarService.findOne({})", id);
        return repository.findOne(id);
    }

    @CacheEvict(value = {"carList"}, allEntries = true)
    public Car save(Car car) {
        return repository.save(car);
    }

    @CacheEvict(value = {"carList", "cars"}, allEntries = true)
    public void update(Long id, Car car) {
        Car fromDB = repository.findOne(id);
        fromDB.setBrand(car.getBrand());
        fromDB.setModel(car.getModel());
        repository.save(fromDB);
    }

    @CacheEvict(value = {"carList", "cars"}, allEntries = true)
    public void delete(Long id) {
        repository.delete(id);
    }

}
