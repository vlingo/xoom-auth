describe("When a new role is created", () => {

    var usedRoleData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listRoles")

        cy.get("#addRoleButton").click()

        cy.fixture("newRole").then((role) => {
            usedRoleData = role
            cy.enterRoleDetails(role, ".v-dialog--active")
            cy.get(".v-dialog--active").contains("Save").click()
        })
    })

    it("then he can be found in the roles list.", () => {
        cy.searchListFor("#roles-list", usedRoleData.name)
        cy.get("#roles-list").get(".v-table").should("contain", usedRoleData.description)
    })
})