module rtl(max, min, reset, data, clock);

output wire signed[0:31] max;
output wire signed[0:31] min;

wire clear;
wire load;

input wire signed[0:31] data;
input wire[0:0] reset;
input wire clock;


controller ctrl(clear, load, reset, clock);
datapath dp(min, max, data, clear, load, clock);

endmodule