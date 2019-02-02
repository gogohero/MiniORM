package interfaces;

import java.sql.SQLException;

public interface DBContext<E> {
	Iterable<E> find(Class<E> table);
	Iterable<E> find (Class<E> table, String where);
	
	E findFirst(Class<E> table);
	
	E findFirst(Class<E> table, String where) throws SQLException, InstantiationException, IllegalAccessException;
}
