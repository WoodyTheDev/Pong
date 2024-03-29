package woodythedev.pong.game.gamefield.Collision;

import java.util.Random;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import woodythedev.pong.game.gamefield.InGamePosition;

@Getter
public class Line {
	@RequiredArgsConstructor
	//tan(18) * (16/9)
	public enum Degree  {
		DEGREE_18(18, 0.18276732913100980846267766937101),
		DEGREE_36(36, 0.40868017200301549831620005108284),
		DEGREE_54(54, 0.77421483026503511524155538982486),
		DEGREE_72(72, 1.7311969896610800389457884490207),
		DEGREE_108(108, -1.7311969896610800389457884490207),
		DEGREE_126(126, -0.77421483026503511524155538982486),
		DEGREE_144(144, -0.40868017200301549831620005108284),
		DEGREE_162(162, -0.18276732913100980846267766937101),
		DEGREE_198(198, 0.18276732913100980846267766937101),
		DEGREE_216(216, 0.40868017200301549831620005108284),
		DEGREE_234(234, 0.77421483026503511524155538982486),
		DEGREE_252(252, 1.7311969896610800389457884490207),
		DEGREE_288(288, -1.7311969896610800389457884490207),
		DEGREE_306(306, -0.77421483026503511524155538982486),
		DEGREE_324(324, -0.40868017200301549831620005108284),
		DEGREE_342(342, -0.18276732913100980846267766937101);

		@Getter
		private final int degree;
		@Getter
		private final Double slopeValue;

		public boolean isLeftToRight() {
			return degree < 108 || degree > 252;
		}

		public static Degree fromValue(int value) {
			for (Degree degree : Degree.values()) {
				if (degree.getDegree() == value) {
					return degree;
				}
			}
			throw new IllegalArgumentException("Degree enum: Invalid value: " + value);
		}
	}

	private Degree m;
	// private DegreeEnum nextM;
	private double b;

	public Line() {
		randomDegree();
	}

	public void randomDegree() {
		// m = Degree.DEGREE_216;
		Random random = new Random();
		switch(random.nextInt(16) + 1) {
			case 1:
			m = Degree.DEGREE_18;
			break;
			case 2:
			m = Degree.DEGREE_36;
			break;
			case 3:
			m = Degree.DEGREE_54;
			break;
			case 4:
			m = Degree.DEGREE_72;
			break;
			case 5:
			m = Degree.DEGREE_108;
			break;
			case 6:
			m = Degree.DEGREE_126;
			break;
			case 7:
			m = Degree.DEGREE_144;
			break;
			case 8:
			m = Degree.DEGREE_162;
			break;
			case 9:
			m = Degree.DEGREE_198;
			break;
			case 10:
			m = Degree.DEGREE_216;
			break;
			case 11:
			m = Degree.DEGREE_234;
			break;
			case 12:
			m = Degree.DEGREE_252;
			break;
			case 13:
			m = Degree.DEGREE_288;
			break;
			case 14:
			m = Degree.DEGREE_306;
			break;
			case 15:
			m = Degree.DEGREE_324;
			break;
			case 16:
			m = Degree.DEGREE_342;
			break;
		}
	}

	public void setM(Degree m) {
		this.m = m;
	}

	public void calcB(InGamePosition position) {
		double y = position.getY();
		double x = position.getX();
		x = x * (m.getSlopeValue());
		b = y - x;
	}

	public InGamePosition calcIntersectionXLine() {
		double x = -b / (m.getSlopeValue());
		return new InGamePosition((int) x, 0);
	}

	public InGamePosition calcIntersectionTopLine(int c) {
		double x = (c - b) / (m.getSlopeValue());
		return new InGamePosition((int) x, c);
	}

	public InGamePosition calcIntersectionYLine(int x) {
		double y = (m.getSlopeValue() * -1) * x + b;
		return new InGamePosition(x, (int) y);
	}

	public InGamePosition calcIntersectionRightLine(int x) {
		double y = (m.getSlopeValue()) * x + b;
		return new InGamePosition(x, (int) y);
	}

}
