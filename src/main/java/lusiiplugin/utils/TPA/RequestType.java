package lusiiplugin.utils.TPA;

public enum RequestType {
	TPA {
		@Override
		public String toString() {
			return "To you";
		}
	},
	TPAHERE {
		@Override
		public String toString() {
			return "To them";
		}
	};
}
