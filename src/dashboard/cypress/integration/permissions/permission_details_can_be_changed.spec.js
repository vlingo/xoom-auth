describe("When data of a permission is changed", () => {

    var permissionData
    var newPermissionData

    before(() => {
        cy.standardLogin()
        cy.visit("/#/listPermissions")

        cy.fixture("existingPermission").then((permission) => {
            permissionData = permission
            
            cy.searchListFor("#permissions-list", permissionData.name)
            cy.get("#permissions-list").contains("edit").click()

            cy.fixture("newPermission").then((newPermission) => {
                
                // name can not be edited
                newPermission.name = undefined
                
                newPermissionData = newPermission
                cy.enterPermissionDetails(newPermission, ".v-dialog--active")

                cy.get(".v-dialog--active").contains("Save").click()
            })
        })
    })

    it("then the old data can no longer be found.", () => {        

        cy.searchListFor("#permissions-list", permissionData.name)
        cy.get("#permissions-list").should("not.contain", permissionData.description)
    })

    it("then the new description is to be found.", () => {
        
        cy.searchListFor("#permissions-list", permissionData.name)
        cy.get("#permissions-list").get(".v-table").should("contain", newPermissionData.description)
    })
})