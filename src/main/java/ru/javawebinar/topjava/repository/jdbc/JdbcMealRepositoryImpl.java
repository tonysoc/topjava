package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JdbcMealRepositoryImpl implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepositoryImpl(DataSource dataSource,  JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertMeal = new SimpleJdbcInsert(dataSource).withTableName("meals").usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("dateTime", meal.getDateTime())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("userId", userId);
        if(meal.isNew()) {
            Number newKey = insertMeal.executeAndReturnKey(map);
            meal.setId(newKey.intValue());
        } else {
            namedParameterJdbcTemplate.update(
                    "UPDATE meals SET datetime=:dateTime, description=:descriprion, " +
                    "calories=:calories, user_id=:userId WHERE id=:id", map);
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE  FROM meals WHERE id=?", id) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        int[] args = {id, userId};
        List<Meal> meals = jdbcTemplate.query("SELECT * FROM meals WHERE id=? AND user_id=?", ROW_MAPPER, args);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query("SELECT * FROM meals WHERE user_id=? ORDER BY datetime", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        List<Meal> meals = jdbcTemplate.query("SELECT * FROM meals WHERE user_id=?", ROW_MAPPER, userId);
        return meals.stream()
                .filter(meal -> DateTimeUtil.isBetween(meal.getTime(), DateTimeUtil.parseLocalTime(startDate.toString()), DateTimeUtil.parseLocalTime(endDate.toString())))
                .collect(Collectors.toList());
    }
}
