package me.ssu.spring_rest_api.events;

import org.springframework.data.jpa.repository.JpaRepository;

interface EventRepository extends JpaRepository<Event, Integer> {
}
