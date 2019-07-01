describe("When an existing constraint is deleted", function () {

    var permissionData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listPermissions")

        cy.fixture("existingPermission").then((permission) => {
            permissionData = permission

            cy.searchListFor("#permissions-list", permissionData.name)
            cy.get("#permissions-list").contains(permissionData.description).click()

            cy.get("#permissions-list").get("#constraint-list").contains("delete").click()
            cy.get(".v-dialog--active").contains("Yes").click()
        })
    })

    it("then the constraint can not be found in the constraint list.", function () {

        cy.get("#constraint-list").should("not.contain", permissionData.constraint.name)
    })
})



