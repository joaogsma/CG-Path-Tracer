package renderer.objects;

public class BSDF {
	public final double ka, kd, ks, alpha;
	
	public BSDF(double ka, double kd, double ks, double alpha) {
		this.ka = ka;
		this.kd = kd;
		this.ks = ks;
		this.alpha = alpha;
	}
	
//	public BSDF(double ka, double kd, double ks, double kt, double alpha) {
//		this.ka = ka;
//		this.kd = kd;
//		this.ks = ks;
//		this.kt = kt;
//		this.alpha = alpha;
//		this.idx_refraction = 1;
//	}
}
