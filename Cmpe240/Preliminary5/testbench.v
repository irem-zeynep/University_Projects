`timescale 1ns/1ns
module testbench();

wire [1:0] y;

reg x;
reg rst;
reg clk;


source s(y, x, rst, clk);

initial begin
    $dumpfile("TimingDiagram.vcd");
    $dumpvars(0, s, y, x, rst, clk);
    
    rst = 1;
    x = 0;
    #30;
    rst = 0;
	x = 0;
    #40;
	x = 1;
    #40;
	x = 1;
    #40;
	x = 0;
    #40;
	x = 0;
    #40;
	x = 1;
    #40;
	x = 1;
    #40;
	x = 1;
    #40;
	x = 0;
    #40;
	x = 0;
    #40;
	x = 1;
    #40;
	x = 1;
    #40;
	
	#80;
    
    $finish;
end

always begin	
	clk = 0;
	#20;
	clk = 1;
	#20;
    end

endmodule
