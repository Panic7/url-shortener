package com.flex.url_shortener.mapper;

public interface BaseMapper<Entity, Request, Response> {

    Entity toEntity(Request request);

    Response toResponse(Entity entity);
}