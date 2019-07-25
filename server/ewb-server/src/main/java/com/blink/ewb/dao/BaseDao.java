package com.blink.ewb.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends Repository<T, ID> {

	T findOne(ID paramID);

	List<T> findAll();

	<S extends T> S save(S paramS);

	void delete(ID paramID);

	void delete(T paramT);

}
