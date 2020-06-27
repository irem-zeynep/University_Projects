`timescale 1ns/1ns
module testbench();
wire [1:0] y;
wire [3:0] currentState;
wire [3:0] nextState;
reg x;
reg rst;
reg clk;
parameter inputseq1 = 32'b 10001000101010111010100010101001;
parameter inputseq2 = 32'b 11000001010001010101011000110101;
parameter inputseq3 = 32'b 00101000101000111100001010101000;
parameter inputseq4 = 32'b 11011001000101010100010101000111;
parameter inputseq5 = 32'b 10001010100010101010101101000101;
parameter inputseq6 = 32'b 11111000000101010001010101000111;
integer i;

source s(y, currentState, nextState, x, rst, clk);

initial begin
    $dumpfile("TimingDiagram.vcd");
    $dumpvars(0, y, currentState, nextState, x, rst, clk);
    
    rst = 1;
    x = 0;
	#20;
    rst = 0;
	
    for (i=31; i>=0; i=i-1) begin
        x = inputseq1[i];
        #40;
    end
	
	#20;
	rst = 1;
    x = 0;
    #20;
    rst = 0;
	
	for (i=31; i>=0; i=i-1) begin
		x = inputseq2[i];
		#40;
    end
	
	#20;
	rst = 1;
    x = 0;
    #20;
    rst = 0;
	
	for (i=31; i>=0; i=i-1) begin
		x = inputseq3[i];
		#40;
    end
	
	#20;	
	rst = 1;
    x = 0;
    #20;
    rst = 0;
	
    for (i=31; i>=0; i=i-1) begin
		x = inputseq4[i];
		#40;
    end
	
	#20;
	rst = 1;
    x = 0;
    #20;
    rst = 0;
	
	for (i=31; i>=0; i=i-1) begin
		x = inputseq5[i];
		#40;
    end
	
	#20;
	rst = 1;
    x = 0;
    #20;
    rst = 0;
	
	for (i=31; i>=0; i=i-1) begin
		x = inputseq6[i];
		#40;
    end
	
    $finish;
end

always begin	
	clk = 1;
	#20;
	clk = 0;
	#20;
    end

endmodule
