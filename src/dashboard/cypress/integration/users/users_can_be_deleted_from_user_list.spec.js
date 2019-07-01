describe("When a user is deleted from user list", () => {

    var usedUserData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listUsers")

        cy.fixture("existingUser").then((user) => {
            usedUserData = user
            
            cy.searchListFor("#users-list", usedUserData.userName)

            cy.get("#users-list").contains("delete").click()
            cy.get(".v-dialog--active").contains("Yes").click()
        })
    })

    it("then he can no longer be found.", () => {
        cy.searchListFor("#users-list", usedUserData.userName)

        cy.get("#users-list").get(".v-table").should("not.contain", usedUserData.email)
    })

    it("instead an error text is shown.", () => {
        let errorText = "Your search for \"" + usedUserData.userName + "\" found no results."
        
        cy.searchListFor("#users-list", usedUserData.userName)
        cy.get("#users-list").should("contain", errorText)
    })
})