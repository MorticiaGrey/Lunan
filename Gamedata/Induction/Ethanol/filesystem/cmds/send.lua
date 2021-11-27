local length = tableLength(args)
if length >= 2 then
    local data = {}
    if length >= 3 then
        for i = 3, length, 1 do
            data[#data+1] = args[i]
        end
    end
    if not lunan.sendPacket(args[1], args[2], data) then
        print(args[1] .. ": no such address")
    end
else
    -- Might switch the order on this, idk, text me if you hate it
    print("usage: send [PROTOCOL] [DESTINATION] [DATA...]")
end
