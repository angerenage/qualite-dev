package fr.iut.univparis8.arollet;

import java.sql.Connection;

public abstract class DAO<T> {
	protected Connection connect;

	public DAO(Connection connect) {
		this.connect = connect;
	}

	public abstract boolean create(T obj);

	public abstract boolean delete(T obj);

	public abstract boolean update(T obj);

	public abstract T find(int id);
}
