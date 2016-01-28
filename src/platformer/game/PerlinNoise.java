package platformer.game;

import java.util.Random;

public class PerlinNoise {
	
	private float AMPLITUDE = 30f;
	private float FREQUENCY = 1;
	
	private Random random = new Random();
	private int seed;
	
	public PerlinNoise(){
		this.seed = random.nextInt(1000000000);
	}
	
	public float generateHeight(int x){
		return getInterpolatedNoise(x * FREQUENCY) * AMPLITUDE;
	}
	
	private float getInterpolatedNoise(float x){
		int intX = (int)x;
		float fracX = x - intX;
		
		float v1 = smoothNoise(intX);
		float v2 = smoothNoise(intX + 1);
		
		return cosineInterpolate(v1, v2, fracX);
	}
	private float smoothNoise(int x){
		return getNoise(x)/16 + getNoise(x - 1)/32 + getNoise(x + 1)/32;
	}
	private float getNoise(int x){
		random.setSeed(x * 3498 + seed);
		return random.nextFloat() * 2f - 1f;
	}
	private float cosineInterpolate(float a, float b, float x){
		double ft =  x * Math.PI;
		float f = (float) ((1 - Math.cos(ft)) * 0.5);
		return a*(1-f) + b*f;
	}
	public void setAmplitude(float amplitude){
		AMPLITUDE = amplitude;
	}
	public void setFrequency(float frequency){
		FREQUENCY = frequency;
	}
}