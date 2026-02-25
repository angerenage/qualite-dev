package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:sql/schema-h2.sql", "classpath:sql/dataset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ApiIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWorks() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "alice",
                      "password": "alice-pass"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/annonces")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "A",
                      "description": "B",
                      "address": "C",
                      "mail": "a@b.com",
                      "categoryId": 1
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/categories")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void insufficientRoleReturns403() throws Exception {
        String aliceToken = login("alice", "alice-pass");

        mockMvc.perform(post("/api/annonces/1/archive")
                .header("Authorization", "Bearer " + aliceToken))
            .andExpect(status().isForbidden());
    }

    @Test
    void fullAnnonceCrudFlow() throws Exception {
        String aliceToken = login("alice", "alice-pass");
        String bobToken = login("bob", "bob-pass");

        String createdBody = mockMvc.perform(post("/api/annonces")
                .header("Authorization", "Bearer " + aliceToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Nouvelle annonce",
                      "description": "Description test",
                      "address": "Montreuil",
                      "mail": "owner@example.com",
                      "categoryId": 1
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String id = jsonValue(createdBody, "id");

        mockMvc.perform(get("/api/annonces/{id}", id)
                .header("Authorization", "Bearer " + aliceToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Nouvelle annonce"));

        mockMvc.perform(put("/api/annonces/{id}", id)
                .header("Authorization", "Bearer " + aliceToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Annonce maj",
                      "description": "Description maj",
                      "address": "Paris",
                      "mail": "owner@example.com",
                      "status": "DRAFT",
                      "categoryId": 1,
                      "version": 0
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").value(1));

        mockMvc.perform(patch("/api/annonces/{id}", id)
                .header("Authorization", "Bearer " + aliceToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "description": "Description patch",
                      "version": 1
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Description patch"));

        mockMvc.perform(post("/api/annonces/{id}/archive", id)
                .header("Authorization", "Bearer " + bobToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ARCHIVED"));

        mockMvc.perform(delete("/api/annonces/{id}", id)
                .header("Authorization", "Bearer " + aliceToken))
            .andExpect(status().isNoContent());
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                    "\"username\":\"" + username + "\"," +
                    "\"password\":\"" + password + "\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return jsonValue(body, "token");
    }

    private String jsonValue(String body, String field) {
        String marker = "\"" + field + "\":";
        int start = body.indexOf(marker);
        int valueStart = start + marker.length();
        while (valueStart < body.length() && Character.isWhitespace(body.charAt(valueStart))) {
            valueStart++;
        }
        if (body.charAt(valueStart) == '"') {
            int end = body.indexOf('"', valueStart + 1);
            return body.substring(valueStart + 1, end);
        }
        int end = valueStart;
        while (end < body.length() && Character.isDigit(body.charAt(end))) {
            end++;
        }
        return body.substring(valueStart, end);
    }
}

