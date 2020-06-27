module testbench();

wire signed[0:31] max;
wire signed[0:31] min;

reg signed[0:31] data;
reg reset;
reg clock;

rtl r(max, min, reset, data, clock);

initial begin
    $dumpfile("TimingDiagram.vcd");
    $dumpvars(0, testbench);
	//clock starts from zero, min max and data are uninitialized at the beginning.
	reset = 1;
	reset = 0;
	#10;
	data = 32'd2;
	#10;
	data = 32'd3;
	#10;
	data = -32'd8;
	#10;
	data = 32'd56;
	#10;
	data = -32'd8;
	#10;
	data = 32'd90;
	#10;
	data = -32'd90;
	#10;
	reset = 1; //reset is 1
	#10;
	data = -32'd12;
	#10
	data = 32'd12;
	#10
	data = 32'd25;
	#10
	data = 32'd18;
	#10
	data = 32'd0;
	#10
	reset = 0; //reset is 0
	#10
	data = 32'd3;
	#10
	data = 32'd0;
	#10
	data = -32'd12;
	#10
	reset = 1;  //reset is 1
	#10
	reset = 0;  //reset is 0
	#10
	data = 32'd125;
	#10
	data = 32'd125;
	#10
	data = -32'd125;
	#10
	data = 32'd200;
	#10
    
    $finish;
end

always begin	
	clock = 0;
	#5;
	clock = 1;
	#5;
end

endmodule

