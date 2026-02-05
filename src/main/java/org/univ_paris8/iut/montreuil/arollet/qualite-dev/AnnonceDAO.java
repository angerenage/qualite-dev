package fr.iut.univparis8.arollet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnnonceDAO extends DAO<Annonce> {
	public AnnonceDAO(Connection connect) {
		super(connect);
	}

	@Override
	public boolean create(Annonce obj) {
		String sql = "INSERT INTO annonce (title, description, adress, mail) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, obj.getTitle());
			stmt.setString(2, obj.getDescription());
			stmt.setString(3, obj.getAdress());
			stmt.setString(4, obj.getMail());

			int affected = stmt.executeUpdate();
			if (affected == 0) return false;

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) obj.setId(keys.getInt(1));
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(Annonce obj) {
		if (obj == null) return false;
		return deleteById(obj.getId());
	}

	public boolean deleteById(int id) {
		String sql = "DELETE FROM annonce WHERE id = ?";
		try (PreparedStatement stmt = connect.prepareStatement(sql)) {
			stmt.setInt(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Annonce obj) {
		String sql = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";
		try (PreparedStatement stmt = connect.prepareStatement(sql)) {
			stmt.setString(1, obj.getTitle());
			stmt.setString(2, obj.getDescription());
			stmt.setString(3, obj.getAdress());
			stmt.setString(4, obj.getMail());
			stmt.setInt(5, obj.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Annonce find(int id) {
		String sql = "SELECT id, title, description, adress, mail FROM annonce WHERE id = ?";
		try (PreparedStatement stmt = connect.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return mapRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Annonce> findAll() {
		String sql = "SELECT id, title, description, adress, mail FROM annonce ORDER BY id DESC";
		List<Annonce> annonces = new ArrayList<>();
		try (PreparedStatement stmt = connect.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				annonces.add(mapRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return annonces;
	}

	private Annonce mapRow(ResultSet rs) throws SQLException {
		return new Annonce(
			rs.getInt("id"),
			rs.getString("title"),
			rs.getString("description"),
			rs.getString("adress"),
			rs.getString("mail")
		);
	}
}
