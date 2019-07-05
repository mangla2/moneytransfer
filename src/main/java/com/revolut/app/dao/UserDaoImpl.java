package com.revolut.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.revolut.app.config.DbUtils;
import com.revolut.app.model.User;

public class UserDaoImpl implements UserDao {

	Connection connection = DbUtils.getConnection();
	
	public int create(User user) throws SQLException {
		final String createUser = "INSERT INTO user (firstName, lastName, email) VALUES (?, ?, ?)";
		
		try (PreparedStatement ps = connection.prepareStatement(createUser)){

			ps.setString(1, user.getFirstName());
			ps.setString(2, user.getLastName());
			ps.setString(3, user.getEmail());

			int update = ps.executeUpdate();
			connection.commit();

			return update;
		} catch (SQLException e) {
			connection.rollback();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return 0;
    }
	
	@Override
    public List<User> getAll() throws SQLException {

        final String SELECT_ALL_USERS = "SELECT id, firstName, lastName, email FROM users";

        List<User> users = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USERS)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                );
                users.add(user);
            }
        } catch (SQLException e) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return users;
    }
}
