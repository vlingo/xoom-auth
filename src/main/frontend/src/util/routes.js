import { mdiEllipse, mdiEllipseOutline, mdiFactory, mdiFileDocument, mdiFileDocumentOutline, mdiHome, mdiHomeOutline, mdiStore, mdiStoreOutline, mdiTag, mdiTagOutline } from '@mdi/js';

export default [
	{
		text: 'Home',
		icon: mdiHomeOutline,
		openIcon: mdiHome,
		href: ''
	},
	{
		text: 'Tenants',
		icon: mdiFactory,
		openIcon: mdiFactory,
		href: '/admin/tenants'
	},
];
