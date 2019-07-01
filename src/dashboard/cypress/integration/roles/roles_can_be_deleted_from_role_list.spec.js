describe("When a role is deleted from roles list", () => {

    var usedRoleData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listRoles")

        cy.fixture("existingRole").then((role) => {
            usedRoleData = role
            
            cy.searchListFor("#roles-list", usedRoleData.name)

            cy.get("#roles-list").contains("delete").click()
            cy.get(".v-dialog--active").contains("Yes").click()
        })
    })

    it("then it can no longer be found.", () => {
        cy.searchListFor("#roles-list", usedRoleData.name)

        cy.get("#roles-list").get(".v-table").should("not.contain", usedRoleData.description)
    })

    it("instead an error text is shown.", () => {
        let errorText = "Your search for \"" + usedRoleData.name + "\" found no results."
        
        cy.searchListFor("#roles-list", usedRoleData.name)
        cy.get("#roles-list").should("contain", errorText)
    })
})