local length = tableLength(args)
local out = ""
for i = 1, length, 1 do
    out = out .. " " .. args[i]
end
print(out)
