describe("When a new permission is created", function () {

    var permissionData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listPermissions")


        cy.get('#addPermissionButton').click()

        cy.fixture("newPermission").then((permission) => {
            permissionData = permission
            cy.enterPermissionDetails(permission, ".v-dialog--active")
            cy.enterConstraintDetails(permission.constraint, ".v-dialog--active")

            cy.get(".v-dialog--active").contains("Save").click()

        })
    })

    it("then it can be found in the permission list", function () {
        cy.searchListFor("#permissions-list", permissionData.name)
        cy.get("#permissions-list").get(".v-table").should("contain", permissionData.description)
    })

    it("and it has the entered constraint", function () {
        cy.get("#permissions-list").get(".v-table").contains(permissionData.description).click()
        cy.get("#permissions-list").get(".v-table").should("contain", permissionData.constraint.name)

    })
})


