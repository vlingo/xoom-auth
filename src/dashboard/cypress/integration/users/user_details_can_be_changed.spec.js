describe("When data of a user is changed", () => {

    var usedUserData
    var newUserData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listUsers")

        cy.fixture("existingUser").then((user) => {
            usedUserData = user

            cy.searchListFor("#users-list", usedUserData.userName)
            cy.get("#users-list").contains("edit").click()

            cy.fixture("newUser").then((newUser) => {
                newUserData = newUser
                // Error might occur if we enter credential info because there are no credentials in this view.
                newUser.credential = undefined
                cy.enterUserDetails(newUser, ".v-dialog--active")

                cy.get(".v-dialog--active").contains("Save").click()
            })
        })
    })

    it("then the old data can no longer be found.", () => {
        cy.searchListFor("#users-list", usedUserData.userName)
        cy.get("#users-list").should("not.contain", usedUserData.email)
    })

    it("the the new data is to be found.", () => {

        cy.searchListFor("#users-list", newUserData.userName)

        cy.get("#users-list").within(() => {
            cy.get(".v-table").should("contain", newUserData.userName)
            cy.get(".v-table").should("contain", newUserData.email)
            cy.get(".v-table").should("contain", newUserData.givenName)
            cy.get(".v-table").should("contain", newUserData.familyName)
            cy.get(".v-table").should("contain", newUserData.phone)
        })        
    })
})