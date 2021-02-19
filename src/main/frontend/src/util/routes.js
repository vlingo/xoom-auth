import { mdiAccount, mdiAccountGroup, mdiAccountGroupOutline, mdiAccountOutline, mdiAccountSupervisor, mdiAccountSupervisorOutline, mdiLock, mdiLockOutline, mdiViewDashboard, mdiViewDashboardOutline } from '@mdi/js';

export default [
	{
		text: 'Dashboard',
		icon: mdiViewDashboardOutline,
		openIcon: mdiViewDashboard,
		href: '/admin'
	},
	{
		text: 'Users',
		icon: mdiAccountOutline,
		openIcon: mdiAccount,
		href: '/admin/users'
	},
	{
		text: 'Groups',
		icon: mdiAccountGroupOutline,
		openIcon: mdiAccountGroup,
		href: '/admin/groups'
	},
	{
		text: 'Permissions',
		icon: mdiLockOutline,
		openIcon: mdiLock,
		href: '/admin/permissions'
	},
	{
		text: 'Roles',
		icon: mdiAccountSupervisorOutline,
		openIcon: mdiAccountSupervisor,
		href: '/admin/roles'
	},
];
