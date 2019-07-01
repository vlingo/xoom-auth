describe("When a permission is deleted from permission list", () => {

    var usedPermissionData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listPermissions")

        cy.fixture("existingPermission").then((permission) => {
            usedPermissionData = permission

            cy.searchListFor("#permissions-list", usedPermissionData.name)

            cy.get("#permissions-list").contains("delete").click()
            cy.get(".v-dialog--active").contains("Yes").click()
        })
    })

    it("then it can no longer be found.", () => {
        cy.searchListFor("#permissions-list", usedPermissionData.name)
        cy.get("#permissions-list").get(".v-table").should("not.contain", usedPermissionData.email)
    })

    it("instead an error text is shown.", () => {
        let errorText = "Your search for \"" + usedPermissionData.name + "\" found no results."        

        cy.searchListFor("#permissions-list", usedPermissionData.name)
        cy.get("#permissions-list").should("contain", errorText)
    })
})