package orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import interfaces.DBContext;

public class EntityManager<E> implements DBContext<E> {
	Connection connection;

	public EntityManager(Connection connection) {
		this.connection = connection;
	}

	public Iterable<E> find(Class<E> table) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<E> find(Class<E> table, String where) {
		// TODO Auto-generated method stub
		return null;
	}
	public E findFirst(Class<E> table) {
		// TODO Auto-generated method stub
		return null;
	}
 
	public E findFirst(Class<E> table, String where) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException {
		 Statement stmt = connection.createStatement();
		 String	query = "Select * FROM" + this.getTableName(table) +
				 "WHERE 1 " +  (where != null ? "AND" + where : "") + "LIMIT 1";
		 ResultSet rs = stmt.executeQuery(query);
		 E entity = table.newInstance();
		 rs.next();
		 this.fillEntity(table, rs, entity);
		 return entity;
	}



	private Field getID(Class entity) {
		return Arrays.stream(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class))
				.findFirst().orElseThrow(() -> new UnsupportedOperationException("Entity doesn't have primary key"));
	}
	private void fillEntity(Class<E> table, ResultSet rs, E entity) throws IllegalArgumentException, IllegalAccessException, SQLException{
		
		Field[] fields = table.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			this.fillField(field, entity, rs, field.getAnnotation(Column.class).name());
		}
	}
	private void fillField(Field field, E entity, ResultSet rs, String fieldname) throws IllegalArgumentException, IllegalAccessException, SQLException {
		 field.setAccessible(true);
		 if(field.getType()== int.class || field.getType() == Integer.class) {
			 field.set(entity, rs.getInt(fieldname));
		 }else if(field.getType()== long.class || field.getType() == Long.class) {
			 field.set(entity, rs.getLong(fieldname));
		 }else if(field.getType() == String.class) {
			 field.set(entity, rs.getString(fieldname));
		 }else if(field.getType()== Date.class) {
			 field.set(entity, rs.getDate(fieldname));
		 }
		
	}

	public boolean persist(E entity) throws IllegalAccessException, SQLException {
		Field primary = this.getID(entity.getClass());
		primary.setAccessible(true);
		Object value = primary.get(entity);
		if (value == null || (int) value <= 0) {
			return this.doInsert(entity, primary);
		}
		return this.doUpdate(entity, primary);

	}

	private boolean doInsert(E entity, Field primary) throws SQLException, IllegalAccessException {
		String tableName = this.getTableName(entity.getClass());
		String query = "INSERT INTO " + this.getTableName(entity.getClass()) + " (";
		String columns = "";
		String values = "";

		Field[] fields = entity.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			if (!field.getName().equals(primary.getName())) {
				columns += '`' + this.getFieldName(field) + '`';
				Object value = field.get(entity);
				if (value instanceof Date) {
					values += "'" + new SimpleDateFormat("yyyy-MM-dd").format(value) + "'";
				} else {
					values += "\'" + value + "\'";
				}

				if (i < fields.length - 1) {
					columns += ", ";
					values += ", ";
				}
			}
		}
		query += columns + ") " + "VALUES(" + values + ")";
		return connection.prepareStatement(query).execute();
	}

	private boolean doUpdate(E entity, Field primary) throws SQLException, IllegalArgumentException, IllegalAccessException {
		
		String query = "UPDATE " + this.getTableName(entity.getClass()) + " SET ";
        String fieldsNamesAndValues = "";
        String where = "";
        
        Field[] fields = entity.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if(!field.getName().equals(primary.getName())){
            	fieldsNamesAndValues +=   this.getFieldName(field)+"=";
                Object value = field.get(entity);
                if(value instanceof Date){
                	fieldsNamesAndValues+= "'" + new SimpleDateFormat("yyyy-MM-dd").format(value) + "'";
                }else{
                	fieldsNamesAndValues += "\'" + value + "\'";
                }

                if (i < fields.length - 1) {
                	fieldsNamesAndValues += ", ";
                
                }
            
            }
		}
		return connection.prepareStatement(query).execute();
		
	}

	private String getTableName(Class<?> entity) {
		String tableName = "";
		if (entity.isAnnotationPresent(Entity.class)) {
			Entity annotation = entity.getAnnotation(Entity.class);
			tableName = annotation.name();
		}
		if (tableName.equals("")) {
			tableName = entity.getSimpleName();
		}

		return tableName;
	}

	private String getFieldName(Field field) {
		String fieldName = "";
		if (field.isAnnotationPresent(Column.class)) {
			Column columnAnotation = field.getAnnotation(Column.class);
			fieldName = columnAnotation.name();
		} else {
			fieldName = field.getName();
		}
		return fieldName;
	}
}
